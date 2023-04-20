using DAL.Entities;

namespace DAL.Repositories.Interfaces
{
    public interface ICategoryRepository : IGenericRepository<CategoryEntity, int>
    {
        IQueryable<CategoryEntity> Categories { get; }
    }
}
