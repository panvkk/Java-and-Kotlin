# API Documentation

## Endpoints

### GET /api/movies
**Описание:** Получить список фильмов

#### Параметры запроса (Query)
| Имя | Тип | Обязательный |
|:---|:---|:---:|
| `title` | string | Нет |
| `minimal_rating` | float | Нет |

#### Ответ (200 OK)
`Array<MovieResponseDto>`
```json
[
  {
    "id": 1,
    "title": "string",
    "year": 0,
    "rating": 0.0,
    "director_id": 1,
    "director_name": "string"
  }
]
```

---

### POST /api/movies
**Описание:** Создать фильм

#### Тело запроса (Request Body)
`CreateMovieRequestDto`
```json
{
  "title": "string",
  "year": 0,
  "director_id": 1
}
```

#### Ответ (200 OK)
`MovieResponseDto`
```json
{
  "id": 1,
  "title": "string",
  "year": 0,
  "rating": 0.0,
  "director_id": 1,
  "director_name": "string"
}
```

---

### DELETE /api/movies/{movie_id}
**Описание:** Удалить фильм

#### Параметры пути (Path)
| Имя | Тип | Обязательный |
|:---|:---|:---:|
| `movie_id` | int64 | Да |

#### Ответ (200 OK)
`200 OK (Empty response)`

---

### PATCH /api/movies/{movie_id}
**Описание:** Обновить фильм

#### Параметры пути (Path)
| Имя | Тип | Обязательный |
|:---|:---|:---:|
| `movie_id` | int64 | Да |

#### Тело запроса (Request Body)
`UpdateMovieRequestDto`
```json
{
  "title": "string",
  "year": 0,
  "rating": 0.0
}
```

#### Ответ (200 OK)
`MovieResponseDto`
```json
{
  "id": 1,
  "title": "string",
  "year": 0,
  "rating": 0.0,
  "director_id": 1,
  "director_name": "string"
}
```

---

### GET /api/movies/{director_id}
**Описание:** Получить фильмы режиссёра

#### Параметры пути (Path)
| Имя | Тип | Обязательный |
|:---|:---|:---:|
| `director_id` | int64 | Да |

#### Ответ (200 OK)
`Array<MovieResponseDto>`
```json
[
  {
    "id": 1,
    "title": "string",
    "year": 0,
    "rating": 0.0,
    "director_id": 1,
    "director_name": "string"
  }
]
```

---

### GET /api/directors
**Описание:** Получить список режиссёров

#### Параметры запроса (Query)
| Имя | Тип | Обязательный |
|:---|:---|:---:|
| `director_name` | string | Нет |

#### Ответ (200 OK)
`Array<DirectorResponseDto>`
```json
[
  {
    "id": 1,
    "full_name": "string",
    "birth_year": 0
  }
]
```

---

### POST /api/directors
**Описание:** Создать режиссёра

#### Тело запроса (Request Body)
`CreateDirectorRequestDto`
```json
{
  "full_name": "string",
  "birth_year": 0
}
```

#### Ответ (200 OK)
`DirectorResponseDto`
```json
{
  "id": 1,
  "full_name": "string",
  "birth_year": 0
}
```

---

### DELETE /api/directors/{director_id}
**Описание:** Удалить режиссёра

#### Параметры пути (Path)
| Имя | Тип | Обязательный |
|:---|:---|:---:|
| `director_id` | int64 | Да |

#### Ответ (200 OK)
`200 OK (Empty response)`

---

### PATCH /api/directors/{director_id}
**Описание:** Обновить режиссёра

#### Параметры пути (Path)
| Имя | Тип | Обязательный |
|:---|:---|:---:|
| `director_id` | int64 | Да |

#### Тело запроса (Request Body)
`UpdateDirectorRequestDto`
```json
{
  "full_name": "string",
  "birth_year": 0
}
```

#### Ответ (200 OK)
`DirectorResponseDto`
```json
{
  "id": 1,
  "full_name": "string",
  "birth_year": 0
}
```

---

## Schemas

### CreateMovieRequestDto
| Поле | Тип | Формат | Обязательное |
|:---|:---|:---|:---:|
| `title` | string | - | Да |
| `year` | integer | int32 | Да |
| `director_id` | integer | int64 | Да |

### MovieResponseDto
| Поле | Тип | Формат | Обязательное |
|:---|:---|:---|:---:|
| `id` | integer | int64 | Да |
| `title` | string | - | Да |
| `year` | integer | int32 | Да |
| `rating` | number | float | Да |
| `director_id` | integer | int64 | Да |
| `director_name` | string | - | Да |

### CreateDirectorRequestDto
| Поле | Тип | Формат | Обязательное |
|:---|:---|:---|:---:|
| `full_name` | string | - | Да |
| `birth_year` | integer | int32 | Да |

### DirectorResponseDto
| Поле | Тип | Формат | Обязательное |
|:---|:---|:---|:---:|
| `id` | integer | int64 | Да |
| `full_name` | string | - | Да |
| `birth_year` | integer | int32 | Да |

### UpdateMovieRequestDto
| Поле | Тип | Формат | Обязательное |
|:---|:---|:---|:---:|
| `title` | string | - | Да |
| `year` | integer | int32 | Да |
| `rating` | number | float | Да |

### UpdateDirectorRequestDto
| Поле | Тип | Формат | Обязательное |
|:---|:---|:---|:---:|
| `full_name` | string | - | Да |
| `birth_year` | integer | int32 | Да |
