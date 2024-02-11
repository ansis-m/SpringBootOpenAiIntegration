import React from "react";
import Reply from "../Reply/Reply";
import Input from "../Input/Input";
import "./Page.css";


const Page: React.FC = () => {

    const handleSubmit = (query: string) => {
        window.alert(query);

        const url = new URL("http://localhost:8081/llama/post");
        url.searchParams.append("prompt", query);
        url.searchParams.append("clearContext", "false");

        const eventSource = new EventSource(url);

        eventSource.onmessage = function(event) {
            console.log(event.data);
            if (event.data === "STREAM_END") {
                eventSource.close();
                console.log("closed");
            }
        };

        eventSource.onerror = function(error) {
            console.error("EventSource failed:", error);
            eventSource.close();
        };
    };


    return (
    <div className="Page">
        <Input onSubmit={handleSubmit}/>
        <Reply messages={["one", "two"]}/>
    </div>
    );
};

export default Page;