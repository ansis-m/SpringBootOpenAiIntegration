import React from "react";
import {HeaderProps} from "./props";
import "./Header.css";
import {Utility} from "../../Utils/Utility";
import {debounce, DebouncedFunc} from "lodash";

interface IState {
    name: string;
}
class Header extends React.Component<HeaderProps, IState> {

    private baseUrl = "http://localhost:8081";
    private terminateUrl = new URL(this.baseUrl + "/terminate");
    private saveUrl = new URL(this.baseUrl + "/save");
    private debouncedSaveSession: DebouncedFunc<() => Promise<void>>;
    constructor(props: HeaderProps) {
        super(props);
        this.state = { name: "" };
        this.nameChange = this.nameChange.bind(this);
        this.saveSession = this.saveSession.bind(this);
        this.debouncedSaveSession = debounce(this.saveSession, 600);
    }

    componentDidMount() {
        this.setState({name: this.props.name || ""});
    }

    componentDidUpdate(prevProps: Readonly<HeaderProps>, prevState: Readonly<{}>, snapshot?: any) {
        if (prevProps.name !== this.props.name) {
            this.setState({name: this.props.name || ""});
        }
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

    private readonly saveSession = async () => {
        return Utility.saveSession(this.state.name || "");
    }
    nameChange(event: React.ChangeEvent<HTMLInputElement>): void {
        this.setState({ name: event.target.value });
        this.debouncedSaveSession();
    }

    render() {
        return (
            <div className={"header-grid"}>
                <div className={"terminate"}>
                    <button onClick={this.terminate}>terminate</button>
                </div>
                <div className={"save"}>
                    <input type={"text"} value={this.state.name} onChange={this.nameChange}></input>
                </div>
                <div className={"clear"}>
                    <button onClick={this.clearSession}>New session</button>
                </div>
            </div>
        );
    }
}

export default Header;