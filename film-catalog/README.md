# API

## POST /api/create-movie

Request:
`{
    "title": "Однажды.. в Голливуде", [string]
    "year": 2019, [int]
    "director_id": 123 [long]
}`

## GET /api/movies

Params:
`   "title": "Однажды" [string]
    "minimal_rating": 8.3 [float]`

Response:
`[
    {
        "id": 1 [long],
        "title": "Однажды.. в Голливуде" [string],
        "year": 2019 [int],
        "rating": 0.1 [float],
        "director_id": 123 [long],
        "director_name": "Квентин Тарантино" [string]
    }
]`

## POST /api/create-director

Request:
`{
    "full_name": "Квентин Тарантино" [string],
    "birth_year": 1963 [int]
}`

## GET /api/directors

Params: 
`    "director_name": "Квентин Тарантино" [string]`

Response: 

`[
    {
        "id": 123,
        "full_name": "Квентин Тарантино" [string],
        "birth_year": 1963 [int]
    }
]`
