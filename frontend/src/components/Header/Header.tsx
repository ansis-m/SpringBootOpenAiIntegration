import React from "react";
import {HeaderProps} from "./props";
import "./Header.css";
import {Utility} from "../../Utils/Utility";


class Header extends React.Component<HeaderProps> {

    private baseUrl = "http://localhost:8081";
    private terminateUrl = new URL(this.baseUrl + "/terminate");
    private saveUrl = new URL(this.baseUrl + "/save");
    private readonly inputRef: React.RefObject<HTMLInputElement>;
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

    private clearSession = async () => {
        await this.saveSession();
        await this.props.clearSession();
    }

    private saveSession = async () => {
        const name = this.inputRef.current?.value.trim();
        return Utility.saveSession(name || "");
    }

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
                <div className={"clear"}>
                    <button onClick={this.clearSession}>Clear session</button>
                </div>
            </div>
        );
    }
}

export default Header;