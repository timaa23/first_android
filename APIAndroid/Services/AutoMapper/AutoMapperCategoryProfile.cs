using AutoMapper;
using DAL.Entities;
using Services.Models.Category;

namespace Services.AutoMapper
{
    public class AutoMapperCategoryProfile : Profile
    {
        public AutoMapperCategoryProfile()
        {
            CreateMap<CategoryEntity, CategoryVM>()
                .ForMember(dest => dest.Id,
                opt => opt.MapFrom(src => src.Id))
                .ForMember(dest => dest.Name,
                opt => opt.MapFrom(src => src.Name))
                .ForMember(dest => dest.Image,
                opt => opt.MapFrom(src => src.Image))
                .ForMember(dest => dest.Description,
                opt => opt.MapFrom(src => src.Description))
                .ForMember(dest => dest.Priority,
                opt => opt.MapFrom(src => src.Priority));

            CreateMap<CreateCategoryVM, CategoryEntity>()
                .ForMember(dest => dest.Name,
                opt => opt.MapFrom(src => src.Name))
                .ForMember(dest => dest.Description,
                opt => opt.MapFrom(src => src.Description))
                .ForMember(dest => dest.Priority,
                opt => opt.MapFrom(src => src.Priority))
                .ForMember(dest => dest.DateCreated,
                opt => opt.MapFrom(src => DateTime.SpecifyKind(DateTime.Now, DateTimeKind.Utc)));

            CreateMap<UpdateCategoryVM, CategoryEntity>()
                .ForMember(dest => dest.Name,
                opt => opt.MapFrom(src => src.Name))
                .ForMember(dest => dest.Description,
                opt => opt.MapFrom(src => src.Description))
                .ForMember(dest => dest.Priority,
                opt => opt.MapFrom(src => src.Priority));
        }
    }
}
