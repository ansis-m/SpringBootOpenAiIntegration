import {Message} from "../Page/Page";


export interface InputProps {
    onSubmit: (message: string) => Promise<boolean>;
    message: Message;
}