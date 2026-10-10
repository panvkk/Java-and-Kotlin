# API

# Movies

## POST /api/movies

Request:
```
{
    "title": "Однажды.. в Голливуде", [string]
    "year": 2019, [int]
    "director_id": 123 [long]
}
```

## DELETE /api/movies/{movie_id}

Path Variable:

```
    movie_id: 1 [long]
```

Response: 
// потом допишу)

## GET /api/movies/{director_id}

Path Variable:

```
    director_id: 123 [long]
```

Response:

```
[
    {
        "id": 1 [long],
        "title": "Однажды.. в Голливуде" [string],
        "year": 2019 [int],
        "rating": 0.1 [float],
        "director_id": 123 [long],
        "director_name": "Квентин Тарантино" [string]
    }
]
```

## GET /api/movies

Params:

```   
    "title": "Однажды" [string]
    "minimal_rating": 8.3 [float]
```

Response:

```
[
    {
        "id": 1 [long],
        "title": "Однажды.. в Голливуде" [string],
        "year": 2019 [int],
        "rating": 0.1 [float],
        "director_id": 123 [long],
        "director_name": "Квентин Тарантино" [string]
    }
]
```

# Directors

## POST /api/directors

Request:
```
{
    "full_name": "Квентин Тарантино" [string],
    "birth_year": 1963 [int]
}
```

## GET /api/directors

Params: 
```    
    "director_name": "Квентин Тарантино" [string]
```

Response: 

```
[
    {
        "id": 123,
        "full_name": "Квентин Тарантино" [string],
        "birth_year": 1963 [int]
    }
]
```