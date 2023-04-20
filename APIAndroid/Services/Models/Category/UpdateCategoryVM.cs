using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Models.Category
{
    public class UpdateCategoryVM
    {
        public string Name { get; set; }
        public string Description { get; set; }
        public string ImageBase64 { get; set; }
        public int Priority { get; set; }
    }
}
