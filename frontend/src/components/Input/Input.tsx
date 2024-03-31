import React, {SyntheticEvent, useEffect, useRef, useState} from "react";
import {InputProps} from "./props";
import "./Input.css";
import ReactLoading from "react-loading";


const Input: React.FC<InputProps> = (props) => {

  const [inputText, setInputText] = useState<string>('');
  const [spinner, setSpinner] = useState<boolean>(false);
  const [readonly, setReadonly]= useState<boolean>(false);
  const textareaRef = useRef(null);

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
      setInputText(e.target.value)
      // @ts-ignore
      textareaRef.current.style.height = 'auto';
      // @ts-ignore
      const h = (parseInt(textareaRef.current.scrollHeight) - parseInt(window.getComputedStyle(e.target).fontSize) + 10);
      // @ts-ignore
      textareaRef.current.style.height = h + 'px';
  };

  const  handleKeyDown = (event: React.KeyboardEvent<HTMLTextAreaElement>) => {
      if (event.key === 'Enter' && !event.shiftKey && !readonly) {
            event.preventDefault();
            handleSubmit();
        }
    };

    useEffect(() => {
        setReadonly(props.message.response.length !== 0);
        setInputText(() => props.message.request);
    }, [props]);

  const handleSubmit = () => {
    inputText.trim().length && props.onSubmit(inputText).then(result => {
        setReadonly(result);
        setSpinner(result);});
  }

  return (
      <div className="center">
        <textarea onKeyDown={handleKeyDown} ref={textareaRef} readOnly={readonly} rows={2} value={inputText} onChange={handleInputChange}></textarea>
          {spinner.valueOf() && !props.message.response.length && (
              <div className={"margin_top"}>
                  <ReactLoading type={'spinningBubbles'} color={'#000'} height={20} width={20}/>
              </div>
          )}
      </div>
  );
};

export default Input;