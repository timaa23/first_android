using Services.Models.Category;

namespace Services.Services.Interfaces
{
    public interface ICategoryService
    {
        Task<ServiceResponse> GetAllAsync();
        Task<ServiceResponse> GetByIdAsync(int id);
        Task<ServiceResponse> CreateAsync(CreateCategoryVM model, string token);
        Task<ServiceResponse> UpdateAsync(int id, UpdateCategoryVM model, string token);
        Task<ServiceResponse> DeleteAsync(int id, string token);
    }
}
