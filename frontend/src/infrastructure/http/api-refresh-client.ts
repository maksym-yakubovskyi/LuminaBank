import axios from "axios";

export const refreshClient = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true
})