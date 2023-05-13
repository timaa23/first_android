using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services;
using Services.Models.Category;
using Services.Services.Interfaces;

namespace APIAndroid.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class CategoryController : ControllerBase
    {
        private readonly ICategoryService _categoryService;

        public CategoryController(ICategoryService categoryService)
        {
            _categoryService = categoryService;
        }

        [HttpGet("getAll")]
        public async Task<IActionResult> GetAll()
        {
            var result = await _categoryService.GetAllAsync();
            return SendResponse(result);
        }

        [HttpGet("getById/{id}")]
        public async Task<IActionResult> GetAll(int id)
        {
            var result = await _categoryService.GetByIdAsync(id);
            return SendResponse(result);
        }

        [HttpPost("create")]
        public async Task<IActionResult> Create([FromBody] CreateCategoryVM model)
        {
            var token = await HttpContext.GetTokenAsync("access_token");

            var result = await _categoryService.CreateAsync(model, token);
            return SendResponse(result);
        }

        [HttpPut("edit/{id}")]
        public async Task<IActionResult> Edit(int id, [FromBody] UpdateCategoryVM model)
        {
            var token = await HttpContext.GetTokenAsync("access_token");

            var result = await _categoryService.UpdateAsync(id, model, token);
            return SendResponse(result);
        }

        [HttpDelete("delete/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var token = await HttpContext.GetTokenAsync("access_token");

            var result = await _categoryService.DeleteAsync(id, token);
            return SendResponse(result);
        }

        private IActionResult SendResponse(ServiceResponse response)
        {
            if (response.IsSuccess)
            {
                return Ok(response);
            }
            return BadRequest(response);
        }
    }
}
