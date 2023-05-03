using APIAndroid.Constants;
using DAL.Entities.Identity;
using Microsoft.AspNetCore.Identity;
using Services.Helpers;
using Services.Models.Account;
using Services.Services.Interfaces;

namespace Services.Services.Classes
{
    public class AccountService : IAccountService
    {
        private readonly IJwtTokenService _jwtTokenService;
        private readonly UserManager<UserEntity> _userManager;

        public AccountService(IJwtTokenService jwtTokenService, UserManager<UserEntity> userManager)
        {
            _jwtTokenService = jwtTokenService;
            _userManager = userManager;
        }

        public async Task<ServiceResponse> Login(LoginVM model)
        {
            var user = await _userManager.FindByEmailAsync(model.Email);

            if (user != null)
            {
                var isValidPassword = await _userManager.CheckPasswordAsync(user, model.Password);
                if (isValidPassword)
                {
                    var token = await _jwtTokenService.CreateToken(user);
                    return new ServiceResponse
                    {
                        IsSuccess = true,
                        Message = "Вхід успішно виконаний",
                        Payload = token
                    };
                }
            }

            return new ServiceResponse
            {
                IsSuccess = false,
                Message = "Помилка входу! Не вірний логін або пароль"
            };
        }

        public async Task<ServiceResponse> Register(RegisterUserVM model)
        {
            string imageName = String.Empty;

            if (model.ImageBase64 != null)
            {
                imageName = ImageWorker.SaveImage(model.ImageBase64);
            }

            UserEntity user = new UserEntity()
            {
                FirstName = model.FirstName,
                LastName = model.LastName,
                UserName = model.Email,
                Email = model.Email,
                Image = imageName
            };

            var result = await _userManager.CreateAsync(user, model.Password);
            if (result.Succeeded)
            {
                result = await _userManager.AddToRoleAsync(user, Roles.User);
                return new ServiceResponse
                {
                    IsSuccess = true,
                    Message = "Користувач успішно зареєстрований"
                };
            }
            else
            {
                return new ServiceResponse
                {
                    IsSuccess = false,
                    Message = "Помилка реєстрації"
                };
            }
        }
    }
}
