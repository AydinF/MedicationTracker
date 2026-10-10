namespace IlacTakip.Api.Models;

public class Ilac
{
    public int Id { get; set; }
    public string IlacAdi { get; set; } = string.Empty;
    public string Dozaj { get; set; } = string.Empty;
    public string Saat { get; set; } = string.Empty;
    public List<string> Gunler { get; set; } = new();
    public bool AktifMi { get; set; } = true;
}
