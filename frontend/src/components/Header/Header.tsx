import React from "react";
import {HeaderProps} from "./props";
import "./Header.css";


class Header extends React.Component<HeaderProps> {

    private baseUrl = "http://localhost:8081";
    private terminateUrl = new URL(this.baseUrl + "/terminate");
    private saveSession: any;
    constructor(props: HeaderProps) {
        super(props);
        this.state = { count: 0 };
    }


    terminate = async () => {
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



    render() {
        return (
            <div className={"header-grid"}>
                <div className={"terminate"}>
                    <button onClick={this.terminate}>terminate</button>
                </div>
                <div className={"save"}>
                    <input type={"text"}></input>
                    <button onClick={this.saveSession}>Save current session</button>
                </div>
            </div>
        );
    }
}

export default Header;