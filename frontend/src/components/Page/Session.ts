export interface Session {

    id?: string;
    exchanges: Exchange[];
}

export interface Exchange {
    request: string;
    response?: string;
    systemMessage?: string;
    id: string;
}