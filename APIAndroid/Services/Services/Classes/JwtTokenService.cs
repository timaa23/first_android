using DAL.Entities.Identity;
using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using Services.Services.Interfaces;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace Services.Services.Classes
{
    public class JwtTokenService : IJwtTokenService
    {
        private readonly IConfiguration _configuration;
        private readonly UserManager<UserEntity> _userManager;
        public JwtTokenService(IConfiguration configuration, UserManager<UserEntity> userManager)
        {
            _configuration = configuration;
            _userManager = userManager;
        }

        public async Task<string> CreateToken(UserEntity user)
        {
            IList<string> roles = await _userManager.GetRolesAsync(user);
            List<Claim> claims = new List<Claim>() { new Claim("firstName", user.FirstName), new Claim("lastName", user.LastName), new Claim("email", user.Email), new Claim("image", user.Image ?? "user.jpg") };

            foreach (var role in roles)
            {
                claims.Add(new Claim("roles", role));
            }

            TokenHandler th = new JwtSecurityTokenHandler();

            var signInKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration.GetValue<String>("JWTSecretKey")));
            var signInCredentials = new SigningCredentials(signInKey, SecurityAlgorithms.HmacSha256);

            var jwt = new JwtSecurityToken(signingCredentials: signInCredentials, expires: DateTime.Now.AddDays(10), claims: claims);
            return new JwtSecurityTokenHandler().WriteToken(jwt);
        }

        public async Task<IList<string>> GetUserRoles(UserEntity user)
        {
            return await _userManager.GetRolesAsync(user);
        }

        public async Task<UserEntity> GetUser(string token)
        {
            if (token == null)
                return null;

            var th = new JwtSecurityTokenHandler();
            var jwst = th.ReadJwtToken(token);
            var jti = jwst.Claims.First(claim => claim.Type == "email").Value;

            var user = await _userManager.FindByEmailAsync(jti);
            return user;
        }
    }
}
