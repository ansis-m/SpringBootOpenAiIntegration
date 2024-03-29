import {Message} from "../Page/Page";


export interface InputProps {
    onSubmit: (message: string) => void;
    message: Message;
}