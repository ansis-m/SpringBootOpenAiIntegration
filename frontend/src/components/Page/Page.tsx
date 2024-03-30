import React, {useEffect, useRef, useState} from "react";
import Reply from "../Reply/Reply";
import Input from "../Input/Input";
import "./Page.css";
import {Exchange, Session} from "./Session";

const Page: React.FC = () => {

    const [cookieIsSet, setCookie] = useState(false);
    let connection = useRef<EventSource | null>(null);
    const baseUrl = "http://localhost:8081";
    const messageUrl = new URL(baseUrl + "/llama/post");
    const connectionUrl = new URL(baseUrl + "/connection");
    const loadUrl = new URL(baseUrl + "/load");
    const terminateUrl = new URL(baseUrl + "/terminate");
    const [messages, setMessage] = useState<Message[]>([{"request": "", "response": []}]);


    useEffect(() => {

        if (connection.current === null) {
            connection.current = new EventSource(connectionUrl, { withCredentials: true });

            connection.current.onopen = (event) => {console.log('Connection established!')};

            connection.current.onmessage = function(event) {
                if (event.data === "") {
                    setCookie(true);
                    console.log("dummy message");
                } else if (event.data === "STREAM_END") {
                    setMessage(messages => {
                        return [...messages, {"request": "", "response": []}];
                    })
                    console.log("closed");
                } else {
                    setMessage(messages => {
                        console.log("data: " + event.data);
                        const newMessages = [...messages];
                        const lastIndex = newMessages.length - 1;
                        const lastMessage = {...newMessages[lastIndex]};
                        lastMessage.response = [...lastMessage.response, event.data.replace(/\u00A0/g, " ")];
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
        const fetchMessages = async () => {
            const response = await fetch(loadUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include'
            })

            if (!response.ok) {
                console.log("New session, nothing to load.");
            } else {
                const load: Session = await response.json();
                console.log(JSON.stringify(load));

                const newMessages = load.exchanges.reduce((acc: Message[], curr: Exchange, index: number, src: Exchange[]) => {
                    const message: Message = {id: "", request: curr.request, response: curr.response? Array.of(curr.response) : [], systemMessage: curr.systemMessage};
                    acc.push(message);
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

    const handleSubmit = async (query: string): Promise<boolean> => {

        setMessage(messages => {
            messages[messages.length - 1].request = query;
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
            return false;
        }
        return true;
    };


    const terminate = async () => {
        const response = await fetch(terminateUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include'
        })
        if (!response.ok) {
            window.alert("something wrong with terminate");
        }
    }

    return (
        <div className={"grid-container"}>
            <div className={"header"}>
                <button onClick={terminate}>terminate</button>
            </div>
            <div className="main-content">
                {messages.map((message, index) => {
                    return (
                        <React.Fragment key={index}>
                            <Input onSubmit={handleSubmit}
                                   message={message}/>
                            <Reply message={message}/>
                        </React.Fragment>
                    );
                })}
            </div>
            <div className={"sidebar"}></div>
        </div>
    );
};

export default Page;

export type Message = {
    request: string,
    response: string[],
    id?: string,
    systemMessage?: string;
}