export interface Session {
    name?: string;
    id?: string;
    exchanges: Exchange[];
}

export interface Exchange {
    request: string;
    response?: string;
    systemMessage?: string;
    id: string;
}