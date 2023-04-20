﻿using AutoMapper;
using DAL.Entities;
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
        private readonly IMapper _mapper;

        public CategoryService(ICategoryRepository repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
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
                    Payload = response
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

        public async Task<ServiceResponse> CreateAsync(CreateCategoryVM model)
        {
            try
            {
                var category = _mapper.Map<CategoryEntity>(model);

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

        public async Task<ServiceResponse> UpdateAsync(int id, UpdateCategoryVM model)
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

                var newCategory = _mapper.Map<CategoryEntity>(category);

                ImageWorker.RemoveImage(category.Image);
                category.Image = ImageWorker.SaveImage(model.ImageBase64);

                await _repository.Update(newCategory);

                var response = _mapper.Map<CategoryVM>(newCategory);
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

        public async Task<ServiceResponse> DeleteAsync(int id)
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
    }
}
