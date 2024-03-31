import React, {useRef} from "react";
import {HeaderProps} from "./props";
import "./Header.css";


class Header extends React.Component<HeaderProps> {

    private baseUrl = "http://localhost:8081";
    private terminateUrl = new URL(this.baseUrl + "/terminate");
    private saveUrl = new URL(this.baseUrl + "/save");
    private inputRef: React.RefObject<HTMLInputElement>;
    constructor(props: HeaderProps) {
        super(props);
        this.inputRef = React.createRef<HTMLInputElement>();
        this.state = { count: 0 };
    }


    private terminate = async () => {
        const response = await fetch(this.terminateUrl, {
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

    private saveSession = async () => {
        console.log(this.inputRef?.current?.value);
        const name = this.inputRef?.current?.value.trim();
        if (name) {
          try{
              const response = await fetch(this.saveUrl, {
                  method: "POST",
                  headers: {
                      'Content-Type': 'application/json',
                  },
                  credentials: 'include',
                  body: name
              })
              if (!response.ok) {
                  window.alert("something wrong with terminate");
              }
          } catch (e) {
              window.alert(e)
          }

        }
    };



    render() {
        return (
            <div className={"header-grid"}>
                <div className={"terminate"}>
                    <button onClick={this.terminate}>terminate</button>
                </div>
                <div className={"save"}>
                    <input type={"text"} ref={this.inputRef}></input>
                    <button onClick={this.saveSession}>Save current session</button>
                </div>
            </div>
        );
    }
}

export default Header;