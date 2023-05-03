using DAL.Entities.Identity;

namespace Services.Services.Interfaces
{
    public interface IJwtTokenService
    {
        Task<string> CreateToken(UserEntity user);
    }
}
