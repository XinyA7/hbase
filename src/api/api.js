import request from "./request"

export async function getMovie(data) {
    return await request.post("/api/getMovie", data);
}

export async function SearchMovieByName(data) {
    return await request.post("/api/SearchMovieByName", data);
}