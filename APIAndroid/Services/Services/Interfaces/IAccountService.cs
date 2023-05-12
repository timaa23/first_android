using Services.Models.Account;

namespace Services.Services.Interfaces
{
    public interface IAccountService
    {
        Task<ServiceResponse> Login(LoginVM model);
        Task<ServiceResponse> Register(RegisterUserVM model);
        Task<ServiceResponse> Update(UpdateUserVM model, string token);
    }
}
