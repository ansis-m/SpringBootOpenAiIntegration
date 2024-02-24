import {ReplyProps} from "./props";
import React from "react";
import "../Input/Input.css";


const Reply: React.FC<ReplyProps> = ({messages}) => {
    return (<div className=""> <pre>{messages.join('')} </pre></div>);
};

export default Reply;