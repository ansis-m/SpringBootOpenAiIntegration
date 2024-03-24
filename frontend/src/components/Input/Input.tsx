import React, {useEffect, useState} from "react";
import {InputProps} from "./props";
import "./Input.css";


const Input: React.FC<InputProps> = (props) => {

  const [inputText, setInputText] = useState<string>('');

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => setInputText(e.target.value);

    useEffect(() => {
        setInputText(() => props.prompt)
    }, []);

  const handleSubmit = () => {
    props.onSubmit(inputText);
    //setInputText('');
  }

  return (
      <div className="center">
        <textarea rows={4} cols={100} value={inputText} onChange={handleInputChange}></textarea>
          <br></br>
        <button onClick={handleSubmit}>Send</button>
      </div>
  );
};

export default Input;