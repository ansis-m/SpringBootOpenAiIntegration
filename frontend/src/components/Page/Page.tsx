import React, {useEffect, useRef, useState} from "react";
import Reply from "../Reply/Reply";
import Input from "../Input/Input";
import "./Page.css";


const Page: React.FC = () => {

    let id = "";
    let connection = useRef<EventSource | null>(null);
    const messageUrl = new URL("http://localhost:8081/llama/post");
    const connectionUrl = new URL("http://localhost:8081/connection");
    const [messages, setMessage] = useState([{"prompt": "", "reply": [""]}]);


    useEffect(() => {

        if (connection.current === null) {
            connection.current = new EventSource(connectionUrl, { withCredentials: true });

            connection.current.onopen = (event) => {console.log('Connection established!')};

            connection.current.onmessage = function(event) {
                if (event.data === "STREAM_END") {
                    setMessage(messages => {
                        return [...messages, {"reply": [""], "prompt": ""}];
                    })
                    console.log("closed");
                } else {
                    setMessage(messages => {
                        console.log(event.data);
                        const newMessages = [...messages];
                        const lastIndex = newMessages.length - 1;
                        const lastMessage = {...newMessages[lastIndex]};
                        lastMessage.reply = [...lastMessage.reply, event.data];
                        newMessages[lastIndex] = lastMessage;
                        return newMessages;
                    });
                }
            };

            connection.current.onerror = function(error) {
                console.error("EventSource failed:", error);
            };
        }

        return () => {
            if (connection.current) {
                connection.current.close();
                connection.current = null;
            }
        };

    }, []);



    const handleSubmit = async (query: string) => {

        setMessage(messages => {
            messages[messages.length - 1].prompt = query;
            return [...messages];
        })

        const response = await fetch(messageUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({"prompt": query, "clearContext": false}),
            credentials: 'include'
        })
    };


    return (
    <div className="Page">
        {messages.map(({prompt, reply}, index) => (
            <React.Fragment key={index}>
                <Input onSubmit={handleSubmit}/><Reply messages={reply}/>
            </React.Fragment>
        ))}

    </div>
    );
};

export default Page;