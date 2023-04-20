using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services
{
    public class ServiceResponse
    {
        public string Message { get; set; }
        public bool IsSuccess { get; set; }
        public object Payload { get; set; }
    }
}
