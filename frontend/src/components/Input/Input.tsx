import React, {useState} from "react";
import {InputProps} from "./props";
import "./Input.css";


const Input: React.FC<InputProps> = (props) => {

  const [inputText, setInputText] = useState<string>('');

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => setInputText(e.target.value);

  const handleSubmit = () => {
    props.onSubmit(inputText);
    //setInputText('');
  }

  return (
      <div className="center">
        <div>
          Input
        </div>
        <textarea value={inputText} onChange={handleInputChange}></textarea>
        <button onClick={handleSubmit}>Send</button>
      </div>
  );
};

export default Input;