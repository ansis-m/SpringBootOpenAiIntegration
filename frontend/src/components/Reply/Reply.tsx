import {ReplyProps} from "./props";
import React from "react";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { solarizedlight } from 'react-syntax-highlighter/dist/esm/styles/prism';
import "../Reply/Reply.css";


const Reply: React.FC<ReplyProps> = ({message}) => {
    return (<div className="response"> {message.reply.join('').split("```").map((part, index) => {
        if (index % 2 === 0 ) {
            return (<div key={index}>{part}</div>);
        }
        return <SyntaxHighlighter key={index} style={solarizedlight} language={"java"}>{part}</SyntaxHighlighter>
    })} </div>);
};

export default Reply;