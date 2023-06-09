﻿using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Mvc;
using Services;
using Services.Models.Account;
using Services.Services.Interfaces;

namespace APIAndroid.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AccountController : ControllerBase
    {
        private readonly IAccountService _accountService;

        public AccountController(IAccountService accountService)
        {
            _accountService = accountService;
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginVM model)
        {
            var result = await _accountService.Login(model);
            return SendResponse(result);
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterUserVM model)
        {

            var result = await _accountService.Register(model);
            return SendResponse(result);
        }

        [HttpPost("update")]
        public async Task<IActionResult> Update([FromBody] UpdateUserVM model)
        {
            var token = await HttpContext.GetTokenAsync("access_token");
            var result = await _accountService.Update(model, token);
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
