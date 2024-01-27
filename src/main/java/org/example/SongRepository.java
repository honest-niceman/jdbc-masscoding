package org.example;

import java.sql.*;

@SuppressWarnings("SqlDialectInspection")
public class SongRepository {
    // 6. Написать метод для запуска prepared statement на отображение всех записей из таблицы с песнями, у которых name будет равен переданному в метод в качестве параметра
    protected static void find(String name) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DbProps.DB_URL, DbProps.DB_USER, DbProps.DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM public.song WHERE name = ?"
             )) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String songId = resultSet.getString("id");
                String songName = resultSet.getString("name");
                String songDuration = resultSet.getString("duration");
                String albumId = resultSet.getString("album_id");
                System.out.printf("Song info: [" +
                        "id = %s, " +
                        "songName = %s, " +
                        "songDuration = %s, " +
                        "albumId = %s]%n", songId, songName, songDuration, albumId);
            }
        }
    }

    // 7. Написать метод для запуска prepared statement на вставка в таблицу с песнями (параметры: имя песни, длительность, id альбома)
    protected static void insert(String nameSong, int durationSong, int idAlbum) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DbProps.DB_URL, DbProps.DB_USER, DbProps.DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO public.song (name, duration, album_id) VALUES (?, ?, ?)"
             )) {

            preparedStatement.setString(1, nameSong);
            preparedStatement.setInt(2, durationSong);
            preparedStatement.setInt(3, idAlbum);

            if (preparedStatement.executeUpdate() == 1) {
                System.out.println("The value inserted successfully");
            } else {
                System.out.println("The value is not inserted");
            }
        }
    }

    // 8. Написать метод для удаления по id из таблицы с песнями
    protected static void delete(int id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DbProps.DB_URL, DbProps.DB_USER, DbProps.DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM public.song WHERE id = ?"
             )) {
            preparedStatement.setInt(1, id);

            if (preparedStatement.executeUpdate() == 1) {
                System.out.println("The value deleted successfully");
            } else {
                System.out.println("The value is not deleted");
            }
        }
    }

    // 9. Написать метод для обновления песни по её имени
    protected static void update(String newName, int newDuration, int newIdAlbum, String oldName) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DbProps.DB_URL, DbProps.DB_USER, DbProps.DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE public.song SET name = ?, duration = ?, album_id = ? WHERE name = ?"
             )) {
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, newDuration);
            preparedStatement.setInt(3, newIdAlbum);
            preparedStatement.setString(4, oldName);

            if (preparedStatement.executeUpdate() == 1) {
                System.out.println("The value updated successfully");
            } else {
                System.out.println("The value is not updated");
            }

        }
    }

    // 10*. Продемонстрировать выполнение запроса, выводящего название альбома и самую короткую композицию среди всех композиций для этого альбома, исключая композиции, для которых данное число менее 5
    protected static void selectShortestSongFromAlbumGreaterThan5MinutesLong(int albumId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DbProps.DB_URL, DbProps.DB_USER, DbProps.DB_PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select al.name as album_name, s.name as song_name, s.duration as song_duration
                            from album al
                                     join song s on al.id = s.album_id
                            where (s.album_id, s.duration) in
                                  (select album_id, min(duration) from song where duration >= 50 group by album_id)
                              and al.id = ?
                            """
            );
            preparedStatement.setInt(1, albumId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String albumName = resultSet.getString("album_name");
                String songName = resultSet.getString("song_name");
                String songDuration = resultSet.getString("song_duration");
                System.out.printf("Song info: [" +
                        "albumName = %s, " +
                        "songName = %s, " +
                        "songDuration = %s]%n", albumName, songName, songDuration);
            }
        }
    }
}
