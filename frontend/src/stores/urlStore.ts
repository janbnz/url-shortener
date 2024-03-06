import { writable } from "svelte/store";
import { goto } from '$app/navigation';
import userStore from "./userStore";

const BASE_URL = "http://localhost:8020"

export function createUrl(originalURL: string): Promise<string> {
    const headers = new Headers({
        'Authorization': `Bearer ${userStore.getToken()}`
    });

    return new Promise((resolve, reject) => {
        fetch(new Request(BASE_URL + "/api/create", {
            method: 'POST',
            body: JSON.stringify({
                "url": originalURL
            }),
            headers: headers
        })).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        }).then(data => {
            resolve(BASE_URL + "/" + data.url);
        }).catch(error => {
            console.error("Error:", error);
            reject(error.message);
        });
    });
}

export function getStats(shortenedURL: string): Promise<StatsResponse> {
    const headers = new Headers({
        'Authorization': `Bearer ${userStore.getToken()}`
    });

    return new Promise((resolve, reject) => {
        fetch(new Request(BASE_URL + "/api/stats/" + shortenedURL, {
            method: 'GET',
            headers: headers
        })).then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(response.statusText);
            }
        }).then(data => {
            resolve(data as StatsResponse);
        }).catch(error => {
            console.error("Error:", error);
            reject(error.message);
        });
    });
}

interface StatsResponse {
    original_url: string;
    redirects: number;
}

export default {

}