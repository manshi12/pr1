import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;

// ... (previous code remains the same)

if (status == 200) {
    System.out.println("Successfully Data received from Nova");
    
    // Check if the content is gzipped
    boolean isGzipped = result.getHeaders().getFirst("Content-Encoding") != null 
                        && result.getHeaders().getFirst("Content-Encoding").contains("gzip");
    
    String responseBody;
    if (isGzipped) {
        // Decompress GZIP content
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(result.getBody().getBytes()));
             InputStreamReader reader = new InputStreamReader(gis)) {
            responseBody = IOUtils.toString(reader);
        } catch (IOException e) {
            System.err.println("Error decompressing GZIP content: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    } else {
        responseBody = result.getBody();
    }
    
    System.out.println("Decompressed response body: " + responseBody);

    ObjectMapper mapperObj = new ObjectMapper();
    mapperObj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
        responseObject = mapperObj.readValue(responseBody, ResponseInternalRatingsEvent.class);
        System.out.println("Successfully parsed JSON");
        System.out.println("responseObject: " + responseObject);
    } catch (JsonProcessingException e) {
        System.err.println("Error parsing JSON: " + e.getMessage());
        e.printStackTrace();
    }

    return responseObject;
} else {
    System.err.println("Unexpected status code: " + status);
    return null;
}
