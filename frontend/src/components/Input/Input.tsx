import React, {useEffect, useRef, useState} from "react";
import {InputProps} from "./props";
import "./Input.css";


const Input: React.FC<InputProps> = (props) => {

  const [inputText, setInputText] = useState<string>('');
  const textareaRef = useRef(null);
  const readonly = props.message.reply.length !== 0;

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
      setInputText(e.target.value)
      // @ts-ignore
      textareaRef.current.style.height = 'auto';
      // @ts-ignore
      const h = (parseInt(textareaRef.current.scrollHeight) - parseInt(window.getComputedStyle(e.target).fontSize) * 1.5);
      // @ts-ignore
      textareaRef.current.style.height = h + 'px';
  };

    useEffect(() => {
        setInputText(() => props.message.prompt);
    }, [props]);

  const handleSubmit = () => {
    props.onSubmit(inputText);
  }

  return (
      <div className="center">
        <textarea ref={textareaRef} readOnly={readonly} rows={0} value={inputText} onInput={handleInputChange}></textarea>
          {!readonly && (
              <>
                  <br></br>
                  <button onClick={handleSubmit}>Send</button>
              </>)}
      </div>
  );
};

export default Input;