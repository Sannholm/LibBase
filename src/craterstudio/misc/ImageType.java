/*
 * Created on 17 mrt 2010
 */

package craterstudio.misc;

public enum ImageType
{
    TGA, BMP, JPG, GIF, PNG;
    
    public String asFileExtention()
    {
        return name().toLowerCase();
    }
    
    public String asContentType()
    {
        return "image/" + name().toLowerCase();
    }
}
