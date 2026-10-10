using Microsoft.AspNetCore.Mvc;
using IlacTakip.Api.Models;

namespace IlacTakip.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class IlaclarController : ControllerBase
{
    // Android testi için başlangıçta hazır bellek içi (In-Memory) örnek veri deposu
    private static readonly List<Ilac> _ilaclar = new()
    {
        new Ilac
        {
            Id = 1,
            IlacAdi = "Aspirin",
            Dozaj = "100 mg - 1 Tablet",
            Saat = "09:00",
            Gunler = new List<string> { "Pazartesi", "Çarşamba", "Cuma" },
            AktifMi = true
        },
        new Ilac
        {
            Id = 2,
            IlacAdi = "Parol",
            Dozaj = "500 mg",
            Saat = "14:00",
            Gunler = new List<string> { "Her gün" },
            AktifMi = true
        },
        new Ilac
        {
            Id = 3,
            IlacAdi = "D Vitamini",
            Dozaj = "1 Damla",
            Saat = "20:00",
            Gunler = new List<string> { "Pazar" },
            AktifMi = false
        }
    };

    private static readonly object _lock = new();

    // GET: api/ilaclar (Tüm ilaçları listele)
    [HttpGet]
    public ActionResult<IEnumerable<Ilac>> GetAll()
    {
        lock (_lock)
        {
            return Ok(_ilaclar.ToList());
        }
    }

    // GET: api/ilaclar/5 (ID'ye göre tek ilaç getir)
    [HttpGet("{id}")]
    public ActionResult<Ilac> GetById(int id)
    {
        lock (_lock)
        {
            var ilac = _ilaclar.FirstOrDefault(i => i.Id == id);
            if (ilac == null)
            {
                return NotFound(new { mesaj = $"ID={id} olan ilaç bulunamadı." });
            }
            return Ok(ilac);
        }
    }

    // POST: api/ilaclar (Yeni ilaç ekle)
    [HttpPost]
    public ActionResult<Ilac> Create([FromBody] Ilac yeniIlac)
    {
        if (yeniIlac == null || string.IsNullOrWhiteSpace(yeniIlac.IlacAdi))
        {
            return BadRequest(new { mesaj = "İlaç adı boş olamaz." });
        }

        lock (_lock)
        {
            var yeniId = _ilaclar.Count > 0 ? _ilaclar.Max(i => i.Id) + 1 : 1;
            yeniIlac.Id = yeniId;
            _ilaclar.Add(yeniIlac);

            return CreatedAtAction(nameof(GetById), new { id = yeniIlac.Id }, yeniIlac);
        }
    }

    // PUT: api/ilaclar/5 (İlaç bilgilerini güncelle)
    [HttpPut("{id}")]
    public IActionResult Update(int id, [FromBody] Ilac guncelIlac)
    {
        if (guncelIlac == null || string.IsNullOrWhiteSpace(guncelIlac.IlacAdi))
        {
            return BadRequest(new { mesaj = "Geçersiz ilaç verisi." });
        }

        lock (_lock)
        {
            var mevcut = _ilaclar.FirstOrDefault(i => i.Id == id);
            if (mevcut == null)
            {
                return NotFound(new { mesaj = $"ID={id} olan ilaç bulunamadı." });
            }

            mevcut.IlacAdi = guncelIlac.IlacAdi;
            mevcut.Dozaj = guncelIlac.Dozaj;
            mevcut.Saat = guncelIlac.Saat;
            mevcut.Gunler = guncelIlac.Gunler ?? new List<string>();
            mevcut.AktifMi = guncelIlac.AktifMi;

            return Ok(mevcut);
        }
    }

    // PATCH: api/ilaclar/5/durum (Alarm aktif/pasif durumunu tek hamlede değiştir)
    [HttpPatch("{id}/durum")]
    public IActionResult ToggleDurum(int id)
    {
        lock (_lock)
        {
            var mevcut = _ilaclar.FirstOrDefault(i => i.Id == id);
            if (mevcut == null)
            {
                return NotFound(new { mesaj = $"ID={id} olan ilaç bulunamadı." });
            }

            mevcut.AktifMi = !mevcut.AktifMi;
            return Ok(mevcut);
        }
    }

    // DELETE: api/ilaclar/5 (İlacı sil)
    [HttpDelete("{id}")]
    public IActionResult Delete(int id)
    {
        lock (_lock)
        {
            var mevcut = _ilaclar.FirstOrDefault(i => i.Id == id);
            if (mevcut == null)
            {
                return NotFound(new { mesaj = $"ID={id} olan ilaç bulunamadı." });
            }

            _ilaclar.Remove(mevcut);
            return NoContent();
        }
    }
}
