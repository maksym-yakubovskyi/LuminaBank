let accessToken: string | null = null;
let tokenType: string | null = null;

export const tokenStorage = {
    getToken: () => accessToken,
    setToken: (token: string | null) => {
        accessToken = token;
    },
    getTokenType: () => tokenType,
    setTokenType: (type: string | null) => {
        tokenType = type;
    },
}