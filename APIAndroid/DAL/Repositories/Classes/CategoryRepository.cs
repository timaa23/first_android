using DAL.Entities;
using DAL.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

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
