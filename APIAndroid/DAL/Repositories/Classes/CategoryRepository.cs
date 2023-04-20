using DAL.Entities;
using DAL.Repositories.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DAL.Repositories.Classes
{
    public class CategoryRepository : GenericRepository<CategoryEntity, int>, ICategoryRepository
    {
        public CategoryRepository(AppEFContext context)
            : base(context)
        {

        }

        public IQueryable<CategoryEntity> Categories => GetAll();
    }
}
