import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;

// ... (previous code remains the same)

if (status == 200) {
    System.out.println("Successfully Data received from Nova");
    
    byte[] compressedBytes = result.getBody().getBytes(StandardCharsets.ISO_8859_1);
    System.out.println("Compressed response length: " + compressedBytes.length + " bytes");

    String decompressedJson;
    try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedBytes));
         InputStreamReader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
        decompressedJson = IOUtils.toString(reader);
    } catch (IOException e) {
        System.err.println("Error decompressing GZIP content: " + e.getMessage());
        e.printStackTrace();
        return null;
    }

    System.out.println("Decompressed JSON (first 100 chars): " + 
                       decompressedJson.substring(0, Math.min(100, decompressedJson.length())));

    ObjectMapper mapperObj = new ObjectMapper();
    mapperObj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
        responseObject = mapperObj.readValue(decompressedJson, ResponseInternalRatingsEvent.class);
        System.out.println("Successfully parsed JSON");
        System.out.println("responseObject: " + responseObject);
    } catch (JsonProcessingException e) {
        System.err.println("Error parsing JSON: " + e.getMessage());
        e.printStackTrace();
        
        // Try parsing as a generic map to see the structure
        try {
            Map<String, Object> jsonMap = mapperObj.readValue(decompressedJson, new TypeReference<Map<String, Object>>() {});
            System.out.println("Parsed as generic map: " + jsonMap);
        } catch (JsonProcessingException ex) {
            System.err.println("Error parsing JSON as generic map: " + ex.getMessage());
        }
    }

    return responseObject;
} else {
    System.err.println("Unexpected status code: " + status);
    return null;
}
