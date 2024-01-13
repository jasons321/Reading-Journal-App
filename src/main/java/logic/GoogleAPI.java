
package logic;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volume.VolumeInfo.ImageLinks;
import com.google.api.services.books.model.Volumes;
import java.util.List;
import javax.swing.JOptionPane;


public class GoogleAPI {
    public GoogleAPI(){}
    List<String> authors;
    ImageLinks imageTest; 
    String imageLink;
    String title;
    private String key = "";
    public String checkISBN(String ISBN) { 
        try { 
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            final Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                .setApplicationName("API key 1")
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(key))
                .build();
            String query = ISBN;
            Books.Volumes.List volumesList = books.volumes().list(query);
            Volumes volumes = volumesList.execute();
            if (volumes.getTotalItems() == 0) { 
                return "";
            }
            else { 
                return "Success";
            }

        }
        catch (Exception error) { 
            return "";
        }
    }
    
    public String searchBook(String ISBN) {
        try { 
            //Setting up the query parameters
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            final Books books = new Books.Builder(
            GoogleNetHttpTransport.newTrustedTransport(), 
                    jsonFactory, 
  null)
                    .setApplicationName("API key 1")
                    .setGoogleClientRequestInitializer(
                        new BooksRequestInitializer(key))
                    .build();
            
            //Filtering Results
            String query = ISBN;
            Books.Volumes.List volumesList = books.volumes().list(query);
            Volumes volumes = volumesList.execute();
            
            // If there are no books found
            if (volumes.getTotalItems() == 0) { 
                return "";
            }
            
            // Store the returned data value
            for (Volume volume : volumes.getItems()) {
                Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
                authors = volumeInfo.getAuthors();
                imageLink= volumeInfo.getImageLinks().getThumbnail();
                title = volumeInfo.getTitle();
                return "Success";
            }
        }
        catch (Exception error) { 
            return "";
        }
        return "";
    }
    
    public String getImage() { 
        return imageLink;
    }
    
    public List<String> getAuthors() { 
        return authors;
    }
    
    public String getTitle() { 
        return title;
    }
}
