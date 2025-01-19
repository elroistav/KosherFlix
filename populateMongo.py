import requests
import time

# Base URL
BASE_URL = 'http://localhost:4000/api'
MovieIDS = []
UserIDS = []

# Create users and store the first user's user-id
def create_users():
    global id

    users = ['user1', 'user2', 'user3', 'user4', 'user5', 'user6', 'user7', 'user8', 'user9', 'user10', 'user11', 'user12', 'user13', 'user14', 'user15', 'user16', 'user17', 'user18', 'user19', 'user20']
    user_id = None

    for i, user in enumerate(users):
        json_data = {
            'name': user,
            'userName': f'nick-{i}',
            'email': f'email-{i}@example.com',
            'password': 'password',
            'image': 'https://example.com/profile.png'
        }
        response = requests.post(f'{BASE_URL}/users', json=json_data)

        if response.status_code == 201:
            print(f"User {user} created successfully")
            user_id = response.json().get('_id')
            if i < 18:
                UserIDS.append(user_id)
            else:
                id = user_id
        else:
            print(f"response: {response.status_code}")
            exit()
            print(f"Failed to create user {user}: {response.json()}")

    return user_id


# Create categories
def create_categories(user_id):
    categories = ['Action', 'Comedy', 'Drama', 'Horror', 'Sci-Fi']
    headers = {'user-id': user_id}
    category_ids = []  # Initialize a list to store category IDs

    for category in categories:
        json_data = {'name': category, "promoted" : True}
        response = requests.post(f'{BASE_URL}/categories', json=json_data, headers=headers)

        if response.status_code == 201:
            category_id = response.json().get('_id')
            category_ids.append(category_id)  # Store the category ID
            print(f"Category {category} created successfully with ID: {category_id}")
        else:
            print(f"Failed to create category {category}: {response.json()}")

    return category_ids  # Return the list of category IDs



# Create movies with categories
def create_movies(user_id, category_ids):
    movies = [
        {"title": "Movie One", "length": 120, "director": "Director One", "intId": 1, "releaseDate": "2020-01-01", "subtitles" : ["Hebrew"], "language": "English", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie1.mp4", "categories": [category_ids[0]]},  # Action
        {"title": "Movie Two", "length": 150, "director": "Director Two", "intId": 2, "releaseDate": "2020-02-01", "subtitles" : ["Hebrew"], "language": "English", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie2.mp4", "categories": [category_ids[1], category_ids[0]]},  # Comedy
        {"title": "Movie Three", "length": 110, "director": "Director Three", "intId": 3, "releaseDate": "2020-03-01", "subtitles" : ["Hebrew"], "language": "French", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie3.mp4", "categories": [category_ids[2], category_ids[0]]},  # Drama
        {"title": "Movie Four", "length": 140, "director": "Director Four", "intId": 4, "releaseDate": "2020-04-01", "subtitles" : ["Hebrew"], "language": "Spanish", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie4.mp4", "categories": [category_ids[3], category_ids[0]]},  # Horror
        {"title": "Movie Five", "length": 130, "director": "Director Five", "intId": 5, "releaseDate": "2020-05-01", "subtitles" : ["Hebrew"], "language": "German", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie5.mp4", "categories": [category_ids[4], category_ids[0]]},  # Sci-Fi
        
        # New Movies
        {"title": "Movie Six", "length": 135, "director": "Director Six", "intId": 6, "releaseDate": "2020-06-01", "subtitles" : ["Hebrew"], "language": "Italian", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie6.mp4", "categories": [category_ids[0]]},  # Action
        {"title": "Movie Seven", "length": 125, "director": "Director Seven", "intId": 7, "releaseDate": "2020-07-01", "subtitles" : ["Hebrew"], "language": "English", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie7.mp4", "categories": [category_ids[1], category_ids[0]]},  # Comedy
        {"title": "Movie Eight", "length": 145, "director": "Director Eight", "intId": 8, "releaseDate": "2020-08-01", "subtitles" : ["Hebrew"], "language": "Spanish", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie8.mp4", "categories": [category_ids[2], category_ids[0]]},  # Drama
        {"title": "Movie Nine", "length": 160, "director": "Director Nine", "intId": 9, "releaseDate": "2020-09-01", "subtitles" : ["Hebrew"], "language": "French", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie9.mp4", "categories": [category_ids[3], category_ids[0]]},  # Horror
        {"title": "Movie Ten", "length": 110, "director": "Director Ten", "intId": 10, "releaseDate": "2020-10-01", "subtitles" : ["Hebrew"], "language": "German", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie10.mp4", "categories": [category_ids[4], category_ids[0]]},  # Sci-Fi
        
        # More New Movies
        {"title": "Movie Eleven", "length": 125, "director": "Director Eleven", "intId": 11, "releaseDate": "2020-11-01", "subtitles" : ["Hebrew"], "language": "English", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie11.mp4", "categories": [category_ids[0]]},  # Action
        {"title": "Movie Twelve", "length": 140, "director": "Director Twelve", "intId": 12, "releaseDate": "2020-12-01", "subtitles" : ["Hebrew"], "language": "Spanish", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie12.mp4", "categories": [category_ids[1], category_ids[0]]},  # Comedy
        {"title": "Movie Thirteen", "length": 115, "director": "Director Thirteen", "intId": 13, "releaseDate": "2021-01-01", "subtitles" : ["Hebrew"], "language": "French", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie13.mp4", "categories": [category_ids[2], category_ids[0]]},  # Drama
        {"title": "Movie Fourteen", "length": 150, "director": "Director Fourteen", "intId": 14, "releaseDate": "2021-02-01", "subtitles" : ["Hebrew"], "language": "Italian", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie14.mp4", "categories": [category_ids[3], category_ids[0]]},  # Horror
        {"title": "Movie Fifteen", "length": 120, "director": "Director Fifteen", "intId": 15, "releaseDate": "2021-03-01", "subtitles" : ["Hebrew"], "language": "German", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie15.mp4", "categories": [category_ids[4], category_ids[0]]},  # Sci-Fi
        
        # Even More Movies
        {"title": "Movie Sixteen", "length": 140, "director": "Director Sixteen", "intId": 16, "releaseDate": "2021-04-01", "subtitles" : ["Hebrew"], "language": "Russian", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie16.mp4", "categories": [category_ids[0], category_ids[0]]},  # Action
        {"title": "Movie Seventeen", "length": 150, "director": "Director Seventeen", "intId": 17, "releaseDate": "2021-05-01", "subtitles" : ["Hebrew"], "language": "English", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie17.mp4", "categories": [category_ids[1], category_ids[0]]},  # Comedy
        {"title": "Movie Eighteen", "length": 130, "director": "Director Eighteen", "intId": 18, "releaseDate": "2021-06-01", "subtitles" : ["Hebrew"], "language": "Spanish", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie18.mp4", "categories": [category_ids[2], category_ids[0]]},  # Drama
        {"title": "Movie Nineteen", "length": 125, "director": "Director Nineteen", "intId": 19, "releaseDate": "2021-07-01", "subtitles" : ["Hebrew"], "language": "French", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie19.mp4", "categories": [category_ids[3], category_ids[0]]},  # Horror
        {"title": "Movie Twenty", "length": 135, "director": "Director Twenty", "intId": 20, "releaseDate": "2021-08-01", "subtitles" : ["Hebrew"], "language": "German", "thumbnail": "http://localhost:3000/images/brooklyn-99-thumbnail.png", "videoUrl": "https://example.com/movie20.mp4", "categories": [category_ids[4], category_ids[0]]}  # Sci-Fi
    ]


    movie_ids = []
    headers = {'user-id': user_id}

    for movie in movies:
        response = requests.post(f'{BASE_URL}/movies', json=movie, headers=headers)
        
        # Check the response
        if response.status_code == 201:
            movie_id = response.json().get('_id')
            movie_ids.append(movie_id)
            MovieIDS.append(movie_id)
            print(f"Movie {movie['title']} created successfully with ID: {movie_id}")
        else:
            print(f"Failed to create movie {movie['title']} - Status code: {response.status_code}, Response: {response.json()}")

    return movie_ids

def add_movies_to_watched_list():
    i=0
    with requests.Session() as session:  # Use a persistent session
        for movie_id in MovieIDS:
            for user_id in UserIDS:
                print(f"Adding movie {movie_id} to the watched list for user {user_id}...")
                headers = {'user-id': user_id}
                url = f"{BASE_URL}/movies/{movie_id}/recommend"
                response = session.post(url, headers=headers)

                if response.status_code in [200, 201, 204]:
                    print(f"Movie {movie_id} added to the watched list for user {user_id} successfully.")
                else:
                    print(f"Failed to add movie {movie_id} for user {user_id}: {response.status_code} - {response.json()}")

        for movie_id in MovieIDS:
            i = i+1
            if i < 8:
                print(f"Adding movie {movie_id} to the watched list for user {id}...")
                headers = {'user-id': str(id)}
                url = f"{BASE_URL}/movies/{movie_id}/recommend"
                response = session.post(url, headers=headers)

                if response.status_code in [200, 201, 204]:
                    print(f"Movie {movie_id} added to the watched list for user {id} successfully.")
                else:
                    print(f"Failed to add movie {movie_id} for user {id}: {response.status_code} - {response.json()}")


# Main function to run the script
def main():
    user_id = create_users()
    if user_id:
        category_ids = create_categories(user_id)  # Create categories and get their IDs
        if category_ids:  # Ensure categories were created successfully
            movie_ids = create_movies(user_id, category_ids)  # Create movies with categories
            if movie_ids:
                print("Movies created successfully!")
        else:
            print("Failed to create categories.")
    add_movies_to_watched_list()



if __name__ == "__main__":
    main()
