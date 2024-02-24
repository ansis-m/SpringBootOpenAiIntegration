import React, {useState} from "react";
import Reply from "../Reply/Reply";
import Input from "../Input/Input";
import "./Page.css";


const Page: React.FC = () => {

    let id = "";
    const url = new URL("http://localhost:8081/llama/post");
    const [messages, setMessage] = useState([{"prompt": "", "reply": [""]}]);

    const handleSubmit = (query: string) => {

        setMessage(messages => {
            messages[messages.length - 1].prompt = query;
            return [...messages];
        })
        url.searchParams.append("prompt", query);
        url.searchParams.append("clearContext", "false");

        const eventSource = new EventSource(url, { withCredentials: true });

        eventSource.onmessage = function(event) {
            if (event.data === "STREAM_END") {
                eventSource.close();
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

        eventSource.onerror = function(error) {
            console.error("EventSource failed:", error);
            eventSource.close();
        };
    };


    return (
    <div className="Page">
        {messages.map(({prompt, reply}, index) => (
            <><Input onSubmit={handleSubmit}/><Reply messages={reply}/></>
        ))}

    </div>
    );
};

export default Page;