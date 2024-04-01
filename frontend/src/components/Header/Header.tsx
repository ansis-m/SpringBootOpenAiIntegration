import React from "react";
import {HeaderProps} from "./props";
import "./Header.css";
import {Utility} from "../../Utils/Utility";
import {debounce, DebouncedFunc} from "lodash";
import Select, {SingleValue} from 'react-select';

interface IState {
    name: string;
    options: {value: string, label: string }[];
}
class Header extends React.Component<HeaderProps, IState> {

    private baseUrl = "http://localhost:8081";
    private terminateUrl = new URL(this.baseUrl + "/terminate");
    private saveUrl = new URL(this.baseUrl + "/save");
    private debouncedSaveSession: DebouncedFunc<() => Promise<void>>;
    constructor(props: HeaderProps) {
        super(props);
        this.state = { name: "", options: [] };
        this.nameChange = this.nameChange.bind(this);
        this.saveSession = this.saveSession.bind(this);
        this.debouncedSaveSession = debounce(this.saveSession, 600);
    }

    componentDidMount() {
        this.setState({name: this.props.name || ""});
        this.getOptions(this.props.history);
    }
    private getOptions(history: Record<string, string> | undefined): any[] {
        return history ? [{value: "", label: "dummy"}, ...Object.entries(history).map(([key, value]) => ({
            value: key,
            label: value,
        }))] : [];
    }

    componentDidUpdate(prevProps: Readonly<HeaderProps>, prevState: Readonly<{}>, snapshot?: any) {
        console.log("perv: " + prevProps.name);
        console.log("now: " + this.props.name);
        if (prevProps.name !== this.props.name) {
            this.setState({name: this.props.name || ""});
        }
        if (prevProps.history !== this.props.history) {
            this.setState({options: this.getOptions(this.props.history)});
        }
        this.props.history && Object.entries(this.props.history).forEach(e => console.log(e[0], " ", e[1]));
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
        await this.props.clearSession();
        this.setState({name: ""});
    }

    private handleChange = async (selected: SingleValue<{ value: string; label: string; }>) => {
        console.log(selected?.value + " and " +  selected?.label)
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
                <div className={"select"}>
                    <Select options={this.state.options} onChange={this.handleChange}/>
                </div>
            </div>
        );
    }
}

export default Header;