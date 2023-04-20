using Services.Models.Category;

namespace Services.Services.Interfaces
{
    public interface ICategoryService
    {
        Task<ServiceResponse> GetAllAsync();
        Task<ServiceResponse> GetByIdAsync(int id);
        Task<ServiceResponse> CreateAsync(CreateCategoryVM model);
        Task<ServiceResponse> UpdateAsync(int id, UpdateCategoryVM model);
        Task<ServiceResponse> DeleteAsync(int id);
    }
}
