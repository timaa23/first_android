using DAL.Entities.Identity;
using FluentValidation;
using Microsoft.AspNetCore.Identity;
using Services.Models.Account;

namespace APIAndroid.Validators
{
    public class ValidationLoginVM : AbstractValidator<LoginVM>
    {
        private readonly UserManager<UserEntity> _userManager;
        public ValidationLoginVM(UserManager<UserEntity> userManager)
        {
            _userManager = userManager;
            RuleFor(x => x.Email)
               .NotEmpty().WithMessage("Поле пошта є обов'язковим!")
               .EmailAddress().WithMessage("Пошта є не коректною!")
               .DependentRules(() =>
               {
                   RuleFor(x => x.Email).Must(BeUniqueEmail)
                    .WithMessage("Не вірна пошта!");
               });
            RuleFor(x => x.Password)
                .NotEmpty().WithName("Password").WithMessage("Поле пароль є обов'язковим!")
                .MinimumLength(5).WithName("Password").WithMessage("Поле пароль має містити міннімум 5 символів!");
        }
        private bool BeUniqueEmail(string email)
        {
            return _userManager.FindByEmailAsync(email).Result != null;
        }
    }
}
