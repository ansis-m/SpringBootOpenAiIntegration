import React, {useEffect, useRef, useState} from "react";
import Reply from "../Reply/Reply";
import Input from "../Input/Input";
import "./Page.css";


const Page: React.FC = () => {

    const [cookieIsSet, setCookie] = useState(false);
    let connection = useRef<EventSource | null>(null);
    const baseUrl = "http://localhost:8081";
    const messageUrl = new URL(baseUrl + "/llama/post");
    const connectionUrl = new URL(baseUrl + "/connection");
    const loadUrl = new URL(baseUrl + "/load");
    const [messages, setMessage] = useState<Message[]>([{"prompt": "", "reply": [""]}]);


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
                        console.log("data: " + event.data);
                        setCookie(true);
                        const newMessages = [...messages];
                        const lastIndex = newMessages.length - 1;
                        const lastMessage = {...newMessages[lastIndex]};
                        lastMessage.reply = [...lastMessage.reply, event.data.replace(/\u00A0/g, " ")];
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

    useEffect(() => {
        console.log("cookie is set: " + cookieIsSet);

        const fetchMessages = async () => {
            const response = await fetch(loadUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include'
            })

            if (!response.ok) {
                window.alert("something wrong with post");
            } else {
                const load = await response.json();
                console.log("load: " + JSON.stringify(load));

                const newMessages = load.messages.reduce((acc: { prompt: string; reply: string[]; }[], curr: { type: string; content: string; }, index: number, src: (typeof curr)[]) => {
                    if (curr.type === 'user') {
                        console.log("push: " + curr.content);
                        acc.push({"prompt" : curr.content, "reply" : [index + 1 < src.length && src[index + 1].type === 'assistant'? src[index + 1].content : '222']});
                    }
                    return acc;
                }, []);

                setMessage((messages: any) => {
                    return [...newMessages, ...messages];
                })
            }
        }


        if (cookieIsSet) {
            fetchMessages().then();
        }

    }, [cookieIsSet]);

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

        if (!response.ok) {
            window.alert("something wrong with post");
        }
    };


    return (
    <div className="Page">
        {messages.map(({prompt, reply}, index) => (
            <React.Fragment key={index}>
                <Input onSubmit={handleSubmit}
                        prompt = {prompt}/>
                <Reply messages={reply}/>
            </React.Fragment>
        ))}

    </div>
    );
};

export default Page;

export type Message = {
    prompt: string,
    reply: string[];
}