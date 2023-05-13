using APIAndroid.Constants;
using AutoMapper;
using DAL.Entities;
using DAL.Entities.Identity;
using DAL.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;
using Services.Helpers;
using Services.Models.Category;
using Services.Services.Interfaces;

namespace Services.Services.Classes
{
    public class CategoryService : ICategoryService
    {
        private readonly ICategoryRepository _repository;
        private readonly IJwtTokenService _jwtTokenService;
        private readonly IMapper _mapper;

        public CategoryService(ICategoryRepository repository, IMapper mapper, IJwtTokenService jwtTokenService)
        {
            _repository = repository;
            _mapper = mapper;
            _jwtTokenService = jwtTokenService;
        }

        public async Task<ServiceResponse> GetAllAsync()
        {
            var categories = await _repository.GetAll().OrderBy(x => x.Priority).ToListAsync();
            var response = _mapper.Map<List<CategoryVM>>(categories);

            if (response.Count == 0)
            {
                return new ServiceResponse
                {
                    IsSuccess = true,
                    Message = "Список категорій пустий",
                };
            }

            return new ServiceResponse
            {
                IsSuccess = true,
                Message = "Категорії успішно завантажені",
                Payload = response
            };
        }

        public async Task<ServiceResponse> GetByIdAsync(int id)
        {
            var category = await _repository.GetById(id);

            if (category == null)
            {
                return new ServiceResponse
                {
                    IsSuccess = false,
                    Message = "Категорія з таким ID не знайдена"
                };
            }

            var response = _mapper.Map<CategoryVM>(category);
            return new ServiceResponse
            {
                IsSuccess = true,
                Message = "Категорія успішно завантажена",
                Payload = response
            };
        }

        public async Task<ServiceResponse> CreateAsync(CreateCategoryVM model, string token)
        {
            try
            {
                var user = await _jwtTokenService.GetUser(token);
                if (user == null)
                {
                    return new ServiceResponse
                    {
                        IsSuccess = false,
                        Message = "Помилка токену!",
                    };
                }

                var category = _mapper.Map<CategoryEntity>(model);

                category.User = user;
                category.Image = ImageWorker.SaveImage(model.ImageBase64);

                await _repository.Create(category);

                var response = _mapper.Map<CategoryVM>(category);
                return new ServiceResponse
                {
                    IsSuccess = true,
                    Message = "Категорія успішно створена",
                    Payload = response
                };
            }
            catch (Exception ex)
            {
                return new ServiceResponse
                {
                    IsSuccess = false,
                    Message = "Помилка створення: " + ex.Message
                };
            }
        }

        public async Task<ServiceResponse> UpdateAsync(int id, UpdateCategoryVM model, string token)
        {
            try
            {
                var category = await _repository.GetById(id);
                if (category == null)
                {
                    return new ServiceResponse
                    {
                        IsSuccess = false,
                        Message = "Категорія з таким ID не знайдена"
                    };
                }

                var user = await _jwtTokenService.GetUser(token);
                if (user == null)
                {
                    return new ServiceResponse
                    {
                        IsSuccess = false,
                        Message = "Помилка токену!",
                    };
                }

                if (!await HasRightsToChange(user, category))
                {
                    return new ServiceResponse
                    {
                        IsSuccess = false,
                        Message = "Вибачте, ви не маєте права змінювати категорію"
                    };
                }

                category.Name = model.Name;
                category.Description = model.Description;
                category.Priority = model.Priority;

                if (model.ImageBase64.Length > 0)
                {
                    string image = ImageWorker.SaveImage(model.ImageBase64);
                    ImageWorker.RemoveImage(category.Image);
                    category.Image = image;
                }

                await _repository.Update(category);

                var response = _mapper.Map<CategoryVM>(category);
                return new ServiceResponse
                {
                    IsSuccess = true,
                    Message = "Категорія успішно змінена",
                    Payload = response
                };
            }
            catch (Exception ex)
            {
                return new ServiceResponse
                {
                    IsSuccess = false,
                    Message = "Помилка редагування: " + ex.Message
                };
            }
        }

        public async Task<ServiceResponse> DeleteAsync(int id, string token)
        {
            try
            {
                var category = await _repository.GetById(id);
                if (category == null)
                {
                    return new ServiceResponse
                    {
                        IsSuccess = false,
                        Message = "Категорія з таким ID не знайдена"
                    };
                }

                var user = await _jwtTokenService.GetUser(token);
                if (user == null)
                {
                    return new ServiceResponse
                    {
                        IsSuccess = false,
                        Message = "Помилка токену!",
                    };
                }

                if (!await HasRightsToChange(user, category))
                {
                    return new ServiceResponse
                    {
                        IsSuccess = false,
                        Message = "Вибачте, ви не маєте права змінювати категорію"
                    };
                }

                await _repository.Delete(id);
                ImageWorker.RemoveImage(category.Image);

                return new ServiceResponse
                {
                    IsSuccess = true,
                    Message = "Категорія успішно видалена"
                };
            }
            catch (Exception ex)
            {
                return new ServiceResponse
                {
                    IsSuccess = false,
                    Message = "Помилка видалення: " + ex.Message
                };
            }
        }

        private async Task<bool> HasRightsToChange(UserEntity user, CategoryEntity category)
        {
            return await IsAdmin(user) || category.UserID == user.Id;
        }

        private async Task<bool> IsAdmin(UserEntity user)
        {
            var userRoles = await _jwtTokenService.GetUserRoles(user);
            return userRoles.Any(ur => ur.Equals(Roles.Admin));
        }
    }
}
