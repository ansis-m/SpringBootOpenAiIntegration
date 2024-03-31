import React, {useEffect, useRef, useState} from "react";
import Reply from "../Reply/Reply";
import Input from "../Input/Input";
import Header from "../Header/Header";
import "./Page.css";
import {Exchange, Session} from "./Session";

const Page: React.FC = () => {

    const [cookieIsSet, setCookie] = useState(false);
    let connection = useRef<EventSource | null>(null);
    const baseUrl = "http://localhost:8081";
    const messageUrl = new URL(baseUrl + "/llama/post");
    const connectionUrl = new URL(baseUrl + "/connection");
    const loadUrl = new URL(baseUrl + "/load");
    const clearUrl = new URL(baseUrl + "/clear");
    const scrollRef = useRef<HTMLDivElement>(null);

    const [messages, setMessage] = useState<Message[]>([{"request": "", "response": []}]);


    useEffect(() => {

        if (connection.current === null) {
            connection.current = new EventSource(connectionUrl, { withCredentials: true });
            connection.current.onmessage = function(event) {
                if (event.data === "") {
                    setCookie(true);
                } else if (event.data === "STREAM_END") {
                    setMessage(messages => {
                        return [...messages, {"request": "", "response": []}];
                    })
                } else {
                    setMessage(messages => {
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


    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [messages]);

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

    const clearSession = async () => {
        const response = await fetch(clearUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include'
        });
        if (response.ok) {
            setMessage(() => {return [{"request": "", "response": []}];});
            //TODO get back some data about state
        }

    }


    return (
        <div className={"grid-container"}>
            <div className={"header"}>
                <Header clearSession={clearSession}></Header>
            </div>
            <div className="main-content" ref={scrollRef}>
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