import React, {SyntheticEvent, useEffect, useRef, useState} from "react";
import {InputProps} from "./props";
import "./Input.css";
import ReactLoading from "react-loading";


const Input: React.FC<InputProps> = (props) => {

  const [inputText, setInputText] = useState<string>('');
  const [spinner, setSpinner] = useState<boolean>(false);
  const [readonly, setReadonly]= useState<boolean>(props.message.reply.length !== 0);
  const textareaRef = useRef(null);

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
      setInputText(e.target.value)
      // @ts-ignore
      textareaRef.current.style.height = 'auto';
      // @ts-ignore
      const h = (parseInt(textareaRef.current.scrollHeight) - parseInt(window.getComputedStyle(e.target).fontSize) * 1.5);
      // @ts-ignore
      textareaRef.current.style.height = h + 'px';
  };

  const  handleKeyDown = (event: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (event.key === 'Enter' && !event.shiftKey && !readonly) {
            console.log('Enter key was pressed');
            event.preventDefault();
            handleSubmit();
        }
    };

    useEffect(() => {
        setInputText(() => props.message.prompt);
    }, [props]);

  const handleSubmit = () => {
    props.onSubmit(inputText).then(result => {
        setReadonly(result);
        setSpinner(result);});
  }

  return (
      <div className="center">
        <textarea onKeyDown={handleKeyDown} ref={textareaRef} readOnly={readonly} rows={0} value={inputText} onChange={handleInputChange}></textarea>
        <br></br>
          {!readonly.valueOf() && !spinner.valueOf() && (
              <>
                  <button onClick={handleSubmit}>Send</button>
              </>
          )}
          {spinner.valueOf() && !props.message.reply.length && (
              <>
                  <ReactLoading type={'spinningBubbles'} color={'#000'} height={20} width={20}/>
              </>
          )}
      </div>
  );
};

export default Input;