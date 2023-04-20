using System.Drawing.Imaging;
using System.Drawing;
using Microsoft.AspNetCore.Http;

namespace Services.Helpers
{
    public static class ImageWorker
    {
        private static readonly int[] IMAGE_SIZE = { 32, 150, 300, 600, 1200 };

        public static Bitmap FromBase64StringToImage(this string base64String)
        {
            byte[] byteBuffer = Convert.FromBase64String(base64String);
            try
            {
                using (MemoryStream memoryStream = new MemoryStream(byteBuffer))
                {
                    memoryStream.Position = 0;
                    Image imgReturn;
                    imgReturn = Image.FromStream(memoryStream);
                    memoryStream.Close();
                    byteBuffer = null;
                    return new Bitmap(imgReturn);
                }
            }
            catch { return null; }
        }

        public static string SaveImage(string imageBase64)
        {
            if (imageBase64 == null || imageBase64.Length == 0)
                throw new ArgumentNullException(nameof(imageBase64));
            if (!imageBase64.StartsWith("data:image/"))
                throw new FormatException();

            string fileName = Path.GetRandomFileName() + ".jpg";

            foreach (var size in IMAGE_SIZE)
            {
                string tmpFileName = size + "_" + fileName;

                try
                {
                    string base64 = imageBase64;
                    if (base64.Contains(","))
                        base64 = base64.Split(',')[1];

                    var img = base64.FromBase64StringToImage();

                    string filePath = Path.Combine(Directory.GetCurrentDirectory(), "images", tmpFileName);
                    var saveImage = CompressImage(img, size, size, false);

                    saveImage.Save(filePath, ImageFormat.Jpeg);
                }
                catch
                {
                    throw new Exception("Файл не вдалося зберегти!");
                }
            }

            return fileName;
        }

        public static Bitmap CompressImage(Bitmap originalPic, int maxWidth, int maxHeight, bool watermark = true, bool transperent = false)
        {
            try
            {
                int width = originalPic.Width;
                int height = originalPic.Height;
                int widthDiff = width - maxWidth;
                int heightDiff = height - maxHeight;
                bool doWidthResize = (maxWidth > 0 && width > maxWidth && widthDiff > heightDiff);
                bool doHeightResize = (maxHeight > 0 && height > maxHeight && heightDiff > widthDiff);

                if (doWidthResize || doHeightResize || (width.Equals(height) && widthDiff.Equals(heightDiff)))
                {
                    int iStart;
                    Decimal divider;
                    if (doWidthResize)
                    {
                        iStart = width;
                        divider = Math.Abs((Decimal)iStart / maxWidth);
                        width = maxWidth;
                        height = (int)Math.Round((height / divider));
                    }
                    else
                    {
                        iStart = height;
                        divider = Math.Abs((Decimal)iStart / maxHeight);
                        height = maxHeight;
                        width = (int)Math.Round(width / divider);
                    }
                }
                using (Bitmap outBmp = new Bitmap(width, height, PixelFormat.Format24bppRgb))
                {
                    using (Graphics oGraphics = Graphics.FromImage(outBmp))
                    {
                        //oGraphics.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
                        //oGraphics.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.HighQualityBicubic;
                        oGraphics.Clear(Color.White);
                        oGraphics.DrawImage(originalPic, 0, 0, width, height);
                        //Watermark
                        if (watermark)
                        {
                            using (Image watermarkImage = Image.FromFile(Path.Combine(Directory.GetCurrentDirectory(), "images", "lohika_watermark.png")))
                            using (TextureBrush watermarkBrush = new TextureBrush(watermarkImage))
                            {
                                //Not responsive
                                //int x = (outBmp.Width - watermarkImage.Width - 15);
                                //int y = (outBmp.Height - watermarkImage.Height -15);
                                double imageHeightBrand = Convert.ToDouble(watermarkImage.Height);
                                double imageWidthBrand = Convert.ToDouble(watermarkImage.Width);
                                double ratioBrand = imageWidthBrand / imageHeightBrand;

                                double imageHeightBild = Convert.ToDouble(outBmp.Height); //height of the image to watermark
                                double imageWidthBild = Convert.ToDouble(outBmp.Width);
                                var imageWidthTmpBranding = imageWidthBild * 0.2; //the watermark width, but only 20% size of the image to watermark
                                var imageHeightTmpBranding = imageWidthTmpBranding / ratioBrand; //height of watermark, preserve aspect ratio
                                int imageWidthBranding = Convert.ToInt32(imageWidthTmpBranding); //convert in into int32 (see method below)
                                int imageHeightBranding = Convert.ToInt32(imageHeightTmpBranding);

                                int watermarkX = (int)(imageWidthBild - imageWidthBranding); // Bottom Right
                                int watermarkY = (int)(imageHeightBild - imageHeightBranding);
                                oGraphics.DrawImage(watermarkImage,
                                    new Rectangle(watermarkX, watermarkY, imageWidthBranding, imageHeightBranding),
                                    new Rectangle(0, 0, (int)imageWidthBrand, (int)imageHeightBrand),
                                    GraphicsUnit.Pixel);
                                //watermarkBrush.TranslateTransform(watermarkX, watermarkY);
                                //oGraphics.FillRectangle(watermarkBrush, new Rectangle(new Point(watermarkX, watermarkY), new Size(watermarkImage.Width, watermarkImage.Height)));
                            }
                        }
                        if (transperent)
                        {
                            outBmp.MakeTransparent();
                        }

                        return new Bitmap(outBmp);
                    }
                }
            }
            catch
            {
                return null;
            }
        }

        public static void RemoveImage(string fileName)
        {
            try
            {
                foreach (var size in IMAGE_SIZE)
                {
                    var filePath = Path.Combine(Directory.GetCurrentDirectory(), "images", size + "_" + fileName);
                    string currentFileName = size + "_" + fileName;

                    if (File.Exists(filePath))
                    {

                        File.Delete(filePath);

                        if (!File.Exists(filePath))
                            Console.WriteLine(currentFileName + " Файл був успішно видалений");
                        else
                            Console.WriteLine(currentFileName + " Файл не був видалений");

                    }
                    else
                        Console.WriteLine(currentFileName + " Файл не знайдено");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Помилка видалення файлу: " + ex.ToString());
            }

        }
    }
}
