var builder = WebApplication.CreateBuilder(args);

// Controller servislerini ekliyoruz
builder.Services.AddControllers();
builder.Services.AddOpenApi();

var app = builder.Build();

// Development ortamında OpenAPI / Swagger desteği
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

// Android emülatöründen HTTP ile bağlanabilmek için HTTPS yönlendirmesini devre dışı bırakıyoruz
// app.UseHttpsRedirection();

app.MapControllers();

app.Run();
