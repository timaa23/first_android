using DAL.Entities;
using DAL.Repositories.Interfaces;

namespace APIAndroid
{
    public static class SeederDB
    {
        public static async void SeedData(this IApplicationBuilder app)
        {
            using (var scope = app.ApplicationServices.GetRequiredService<IServiceScopeFactory>().CreateScope())
            {
                var categoryManager = scope.ServiceProvider.GetRequiredService<ICategoryRepository>();
                if (categoryManager.Categories.Count() == 0)
                {
                    var category = new CategoryEntity
                    {
                        Name = "Телефони",
                        Image = "null.jpg",
                        Description = "Телефони на IOS та Android",
                        Priority = 0,
                        DateCreated = DateTime.SpecifyKind(DateTime.Now, DateTimeKind.Utc)
                    };
                    await categoryManager.Create(category);
                }
            }
        }
    }
}
