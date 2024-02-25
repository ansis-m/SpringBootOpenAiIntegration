import {ReplyProps} from "./props";
import React from "react";
import "../Reply/Reply.css";


const Reply: React.FC<ReplyProps> = ({messages}) => {
    return (<div className="response"> {messages.join('')} </div>);
};

export default Reply;