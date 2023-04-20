using Microsoft.AspNetCore.Http;

namespace Services.Models.Category
{
    public class CreateCategoryVM
    {
        public string Name { get; set; }
        public string Description { get; set; }
        public string ImageBase64 { get; set; }
        public int Priority { get; set; }
    }
}
